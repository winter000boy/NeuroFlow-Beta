'use client'

import { useState, useRef } from 'react'
import { Upload, File, X, Loader2 } from 'lucide-react'
import { Button } from '@/components/ui/button'
import { Card } from '@/components/ui/card'

interface FileUploadProps {
  onFileUpload: (file: File) => Promise<void>
  currentFileUrl?: string
  currentFileName?: string
  acceptedTypes: string
  maxSize: number // in MB
  label: string
  description: string
  isLoading?: boolean
}

export function FileUpload({
  onFileUpload,
  currentFileUrl,
  currentFileName,
  acceptedTypes,
  maxSize,
  label,
  description,
  isLoading = false,
}: FileUploadProps) {
  const [dragActive, setDragActive] = useState(false)
  const [error, setError] = useState<string | null>(null)
  const fileInputRef = useRef<HTMLInputElement>(null)

  const validateFile = (file: File): string | null => {
    // Check file size
    if (file.size > maxSize * 1024 * 1024) {
      return `File size must be less than ${maxSize}MB`
    }

    // Check file type
    const acceptedTypesArray = acceptedTypes.split(',').map(type => type.trim())
    const fileExtension = '.' + file.name.split('.').pop()?.toLowerCase()
    const mimeType = file.type

    const isValidType = acceptedTypesArray.some(type => {
      if (type.startsWith('.')) {
        return fileExtension === type
      }
      return mimeType.startsWith(type.replace('*', ''))
    })

    if (!isValidType) {
      return `File type not supported. Accepted types: ${acceptedTypes}`
    }

    return null
  }

  const handleFile = async (file: File) => {
    setError(null)
    
    const validationError = validateFile(file)
    if (validationError) {
      setError(validationError)
      return
    }

    try {
      await onFileUpload(file)
    } catch (err) {
      const error = err as { response?: { data?: { message?: string } } }
      setError(error.response?.data?.message || 'Failed to upload file')
    }
  }

  const handleDrag = (e: React.DragEvent) => {
    e.preventDefault()
    e.stopPropagation()
    if (e.type === 'dragenter' || e.type === 'dragover') {
      setDragActive(true)
    } else if (e.type === 'dragleave') {
      setDragActive(false)
    }
  }

  const handleDrop = (e: React.DragEvent) => {
    e.preventDefault()
    e.stopPropagation()
    setDragActive(false)

    if (e.dataTransfer.files && e.dataTransfer.files[0]) {
      handleFile(e.dataTransfer.files[0])
    }
  }

  const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    e.preventDefault()
    if (e.target.files && e.target.files[0]) {
      handleFile(e.target.files[0])
    }
  }

  const openFileDialog = () => {
    fileInputRef.current?.click()
  }

  const removeFile = () => {
    setError(null)
    if (fileInputRef.current) {
      fileInputRef.current.value = ''
    }
  }

  return (
    <div className="space-y-2">
      <label className="text-sm font-medium">{label}</label>
      
      <Card className="p-4">
        {currentFileUrl || currentFileName ? (
          <div className="flex items-center justify-between p-4 bg-gray-50 rounded-lg">
            <div className="flex items-center space-x-3">
              <File className="h-8 w-8 text-blue-500" />
              <div>
                <p className="font-medium text-sm">
                  {currentFileName || 'Current file'}
                </p>
                {currentFileUrl && (
                  <a
                    href={currentFileUrl}
                    target="_blank"
                    rel="noopener noreferrer"
                    className="text-xs text-blue-600 hover:underline"
                  >
                    View file
                  </a>
                )}
              </div>
            </div>
            <Button
              type="button"
              variant="ghost"
              size="sm"
              onClick={removeFile}
              disabled={isLoading}
            >
              <X className="h-4 w-4" />
            </Button>
          </div>
        ) : (
          <div
            className={`border-2 border-dashed rounded-lg p-6 text-center transition-colors ${
              dragActive
                ? 'border-primary bg-primary/5'
                : 'border-gray-300 hover:border-gray-400'
            }`}
            onDragEnter={handleDrag}
            onDragLeave={handleDrag}
            onDragOver={handleDrag}
            onDrop={handleDrop}
          >
            {isLoading ? (
              <div className="flex flex-col items-center space-y-2">
                <Loader2 className="h-8 w-8 animate-spin text-primary" />
                <p className="text-sm text-gray-600">Uploading...</p>
              </div>
            ) : (
              <div className="flex flex-col items-center space-y-2">
                <Upload className="h-8 w-8 text-gray-400" />
                <div>
                  <button
                    type="button"
                    onClick={openFileDialog}
                    className="text-primary hover:underline font-medium"
                  >
                    Click to upload
                  </button>
                  <span className="text-gray-600"> or drag and drop</span>
                </div>
                <p className="text-xs text-gray-500">{description}</p>
              </div>
            )}
          </div>
        )}

        <input
          ref={fileInputRef}
          type="file"
          accept={acceptedTypes}
          onChange={handleChange}
          className="hidden"
        />

        {error && (
          <div className="mt-2 p-2 text-sm text-red-600 bg-red-50 border border-red-200 rounded">
            {error}
          </div>
        )}
      </Card>
    </div>
  )
}